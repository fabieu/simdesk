create table session_dg_tmp
(
    id                 INTEGER primary key,
    session_type       VARCHAR(10)                                      not null,
    race_weekend_index INTEGER                                          not null,
    server_name        TEXT                                             not null,
    track_id           VARCHAR(32)                                      not null,
    wet_session        BOOLEAN                                          not null,
    car_count          INTEGER                                          not null,
    session_datetime   TIMESTAMP default ((UNIXEPOCH('subsec') * 1000)) not null,
    file_checksum      VARCHAR(64)                                      not null unique,
    file_name          VARCHAR(64)                                      not null,
    file_content       TEXT                                             not null,
    insert_datetime    TIMESTAMP default ((UNIXEPOCH('subsec') * 1000)) not null
);

insert into session_dg_tmp(id, session_type, race_weekend_index, server_name, track_id, wet_session, car_count,
                           session_datetime, file_checksum, file_name, file_content, insert_datetime)
select id,
       session_type,
       race_weekend_index,
       server_name,
       track_id,
       wet_session,
       car_count,
       session_datetime,
       file_checksum,
       file_name,
       file_content,
       insert_datetime
from session;

drop table session;

alter table session_dg_tmp
    rename to session;

create index ix_sessions_insert_datetime
    on session (insert_datetime);

create index ix_sessions_session_datetime
    on session (session_datetime);

create index ix_sessions_track_id
    on session (track_id);

