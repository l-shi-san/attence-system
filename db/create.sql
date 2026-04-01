create table "user"
(
    id          serial
        primary key,
    username    varchar(50)                                      not null
        unique,
    password    varchar(100)                                     not null,
    real_name   varchar(50)                                      not null,
    role        varchar(20) default 'TEACHER'::character varying not null,
    create_time timestamp   default CURRENT_TIMESTAMP
);

comment on table "user" is '用户表';

comment on column "user".id is '主键ID';

comment on column "user".username is '用户名';

comment on column "user".password is '密码（BCrypt加密）';

comment on column "user".real_name is '真实姓名';

comment on column "user".role is '角色：TEACHER/ADMIN/STUDENT';

comment on column "user".create_time is '创建时间';

alter table "user"
    owner to postgres;

create table course
(
    course_id      varchar(20)  not null
        primary key,
    course_name    varchar(100) not null,
    class_name     varchar(50)  not null,
    teacher_id     bigint       not null
        references "user",
    classroom_name varchar(50),
    rows           smallint,
    cols           smallint,
    exclude_seats  varchar(200),
    weekday        smallint
        constraint course_weekday_check
            check ((weekday >= 1) AND (weekday <= 7)),
    start_week     integer,
    end_week       integer,
    create_time    timestamp default CURRENT_TIMESTAMP
);

comment on table course is '课程表';

comment on column course.course_id is '课程编号（主键）';

comment on column course.course_name is '课程名称';

comment on column course.class_name is '班级名称';

comment on column course.teacher_id is '教师ID';

comment on column course.classroom_name is '教室名称';

comment on column course.rows is '教室行数';

comment on column course.cols is '教室列数';

comment on column course.exclude_seats is '不可坐的座位位置（格式：row,col;row,col）';

comment on column course.weekday is '星期几（1-7）';

comment on column course.start_week is '开始周次';

comment on column course.end_week is '结束周次';

comment on column course.create_time is '创建时间';

alter table course
    owner to postgres;

create table course_selection
(
    id           serial
        primary key,
    student_id   varchar(20) not null,
    student_name varchar(50) not null,
    gender       char(2),
    course_id    varchar(20) not null
        references course,
    select_time  timestamp default CURRENT_TIMESTAMP
);

comment on table course_selection is '选课表';

comment on column course_selection.id is '主键ID';

comment on column course_selection.student_id is '学号';

comment on column course_selection.student_name is '学生姓名';

comment on column course_selection.gender is '性别';

comment on column course_selection.course_id is '课程编号';

comment on column course_selection.select_time is '选课时间';

alter table course_selection
    owner to postgres;

create index idx_course_selection_course_id
    on course_selection (course_id);

create table attendance
(
    id            serial
        primary key,
    student_id    varchar(20)                                     not null,
    student_name  varchar(50)                                     not null,
    course_id     varchar(20)                                     not null,
    check_in_time timestamp                                       not null,
    seat_row      smallint,
    seat_col      smallint,
    status        varchar(20) default 'NORMAL'::character varying not null,
    ip            varchar(15),
    create_time   timestamp   default CURRENT_TIMESTAMP
);

comment on table attendance is '考勤记录表';

comment on column attendance.id is '主键ID';

comment on column attendance.student_id is '学号';

comment on column attendance.student_name is '学生姓名';

comment on column attendance.course_id is '课程编号';

comment on column attendance.check_in_time is '签到时间';

comment on column attendance.seat_row is '座位行号';

comment on column attendance.seat_col is '座位列号';

comment on column attendance.status is '状态：NORMAL正常/LATE迟到/EARLY早退/ABSENT缺勤';

comment on column attendance.ip is '签到IP地址';

comment on column attendance.create_time is '创建时间';

alter table attendance
    owner to postgres;

create index idx_attendance_course_id
    on attendance (course_id);

create index idx_attendance_student_id
    on attendance (student_id);

