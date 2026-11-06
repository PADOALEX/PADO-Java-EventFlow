CREATE TABLE events (
    id uuid PRIMARY KEY,
    name varchar(120) NOT NULL CHECK (length(trim(name)) > 0),
    description varchar(2000),
    event_date date NOT NULL CHECK (event_date > DATE '0001-01-01'),
    start_time time,
    location varchar(160) NOT NULL CHECK (length(trim(location)) > 0),
    status varchar(16) NOT NULL CHECK (status IN ('Planned', 'Completed')),
    created_at_utc timestamptz NOT NULL,
    updated_at_utc timestamptz NOT NULL
);
CREATE INDEX ix_events_date_time_id ON events(event_date, start_time, id);
CREATE INDEX ix_events_status_date_id ON events(status, event_date, id);
CREATE INDEX ix_events_name_id ON events(name, id);
