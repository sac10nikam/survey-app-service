CREATE SCHEMA IF NOT EXISTS mlsurvey;

CREATE TABLE IF NOT EXISTS mlsurvey.users (
  id bigint(20) NOT NULL,
  name varchar(40) NOT NULL,
  username varchar(15) NOT NULL,
  email varchar(40) NOT NULL,
  password varchar(100) NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
);


CREATE TABLE IF NOT EXISTS mlsurvey.roles (
  id bigint(20) NOT NULL,
  name varchar(60) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_name (name)
);


CREATE TABLE IF NOT EXISTS mlsurvey.user_roles (
  user_id bigint(20) NOT NULL,
  role_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id,role_id),
  CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS mlsurvey.questions (
  id bigint(20) NOT NULL,
  question varchar(140) NOT NULL,
  expiration_date_time datetime NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
  created_by bigint(20) DEFAULT NULL,
  updated_by bigint(20) DEFAULT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS mlsurvey.answers (
  id bigint(20) NOT NULL,
  text varchar(40) NOT NULL,
  question_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_answers_question_id FOREIGN KEY (question_id) REFERENCES questions (id)
);


CREATE TABLE IF NOT EXISTS mlsurvey.votes (
  id bigint(20) NOT NULL,
  user_id bigint(20) NOT NULL,
  question_id bigint(20) NOT NULL,
  answers varchar(1000) NOT NULL,
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_votes_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_votes_question_id FOREIGN KEY (question_id) REFERENCES questions (id)
);
