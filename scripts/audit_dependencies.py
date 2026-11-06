import json
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path


def packages(node):
    found = set()
    if node.get("scope"):
        found.add((node["groupId"] + ":" + node["artifactId"], node["version"]))
    for child in node.get("children", []):
        found.update(packages(child))
    return found


tree = json.loads(Path(sys.argv[1]).read_text(encoding="utf-8"))
coordinates = sorted(packages(tree))
if not coordinates:
    raise SystemExit("Dependency inventory is empty")
queries = [{"package": {"ecosystem": "Maven", "name": name}, "version": version}
           for name, version in coordinates]
body = json.dumps({"queries": queries}).encode()
for attempt in range(3):
    try:
        request = urllib.request.Request("https://api.osv.dev/v1/querybatch", data=body,
                                         headers={"Content-Type": "application/json"}, method="POST")
        with urllib.request.urlopen(request, timeout=30) as response:
            results = json.load(response)["results"]
        break
    except (urllib.error.URLError, TimeoutError):
        if attempt == 2:
            raise
        time.sleep(attempt + 1)
if len(results) != len(queries):
    raise SystemExit("OSV response does not match the dependency inventory")
findings = []
for coordinate, result in zip(coordinates, results):
    for vulnerability in result.get("vulns", []):
        findings.append({"package": coordinate[0], "version": coordinate[1], "id": vulnerability["id"]})
print(json.dumps({"source": "OSV", "packages_checked": len(coordinates), "findings": findings}, indent=2))
if findings:
    raise SystemExit(1)
