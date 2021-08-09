create table if not exists user(ID UUID, EMAIL VARCHAR(50), PASSWORD VARCHAR(100), SALT VARCHAR(50), PRIMARY KEY (EMAIL));
create table if not exists workout(ID UUID, NAME VARCHAR(50), GOAL VARCHAR(50), PRIMARY KEY (ID));
create table if not exists user_to_workout(USER_EMAIL VARCHAR(50), ID UUID REFERENCES workout ON DELETE CASCADE);
