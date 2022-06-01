INSERT INTO `EMPLOYEE` (`name`, salary, department) VALUES
('employee1', 100, 'technology'),
('employee2', 80, 'finance'),
('employee3', 110, 'sales')
;

INSERT INTO `ACCOUNT` (`name`, password, admin_flag) VALUES
  ('admin1', '{bcrypt}$2a$10$DcQmBE4eIXgNvkbJs4rL7e3EN/bw5VNzhPRdOLAlju65pVC/ptOOK', TRUE),
  ('admin2', '{bcrypt}$2a$10$uJkW1jXyIzQ5CJHgC/E5w.K1sgdDZG7s.JXzsnMRLfjfwLWDdXGne', TRUE),
  ('admin3', '{bcrypt}$2a$10$KN78780I8nFUWs8N0KNk5eK2EMZKsHsVIn6PCli4DMYToiwZXSVTO', TRUE),
  ('user1', '{bcrypt}$2a$10$ulWR/pfp3B5laFDvA7agg.EJbTJhWvzH3EY997GTNHxULnO0I6TD6', FALSE),
  ('user2', '{bcrypt}$2a$10$QxTkvHNWlP2f7wSM2nco6uqIrSUqe8.2zasktv4XsgU468mcKpUBy', FALSE),
  ('user3', '{bcrypt}$2a$10$0RkbAldGYZkeWXvALE2BKeyRKdXlUtmBP89VM2MdmM5JIou8Mxhru', FALSE)
;
