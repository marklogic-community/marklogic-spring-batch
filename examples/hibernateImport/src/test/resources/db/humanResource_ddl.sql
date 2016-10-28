DROP TABLE regions IF EXISTS CASCADE;
DROP TABLE countries IF EXISTS CASCADE;
DROP TABLE locations IF EXISTS CASCADE;
DROP TABLE departments IF EXISTS CASCADE;
DROP TABLE jobs IF EXISTS CASCADE;
DROP TABLE employees IF EXISTS CASCADE;
DROP TABLE job_history IF EXISTS CASCADE;


CREATE TABLE regions (region_id INTEGER PRIMARY KEY, region_name VARCHAR(25));

CREATE TABLE countries (
     country_id CHAR(2) PRIMARY KEY,
     country_name VARCHAR(40),
     region_id INTEGER /*,
     CONSTRAINT countries_regions_region_id FOREIGN KEY (region_id) REFERENCES regions(region_id) */);

CREATE TABLE locations (
	location_id INTEGER PRIMARY KEY,
	street_address VARCHAR(40),
	postal_code VARCHAR(12),
	city VARCHAR(30),
	state_province VARCHAR(25),
	country_id CHAR(2) /*,
	CONSTRAINT locations_countries_country_id FOREIGN KEY (country_id) REFERENCES countries(country_id) */
	); 

CREATE TABLE departments (
	department_id INTEGER PRIMARY KEY,
	department_name VARCHAR(30),
	manager_id INTEGER,
	location_id INTEGER /*,
	CONSTRAINT departments_locations_location_id FOREIGN KEY (location_id) REFERENCES locations(location_id)  */
	);

CREATE TABLE jobs (
	job_id VARCHAR(10) PRIMARY KEY,
	job_title VARCHAR(35),
	min_salary DECIMAL(8, 0),
	max_salary DECIMAL(8, 0)
	);

CREATE TABLE employees (
	employee_id INTEGER PRIMARY KEY,
	first_name VARCHAR(20),
	last_name VARCHAR(25),
	email VARCHAR(25),
	phone_number VARCHAR(20),
	hire_date DATE,
	job_id VARCHAR(10),
	salary DECIMAL(8, 2),
	commission_pct DECIMAL(2, 2),
	manager_id INTEGER,
	department_id INTEGER /*,
	CONSTRAINT employees_jobs_job_id FOREIGN KEY (job_id) REFERENCES jobs(job_id),
	CONSTRAINT employees_departments_department_id FOREIGN KEY (department_id) REFERENCES departments(department_id),
	CONSTRAINT employees_employees_employee_id FOREIGN KEY (manager_id) REFERENCES employees(employee_id) */
	);
/*
ALTER TABLE departments ADD FOREIGN KEY (manager_id) REFERENCES employees (employee_id);
*/
CREATE TABLE job_history (
	employee_id INTEGER,
	start_date DATE,
	end_date DATE,
	job_id VARCHAR(10),
	department_id INTEGER /*,
	CONSTRAINT job_history_employees_employee_id FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
	CONSTRAINT job_history_jobs_job_id FOREIGN KEY (job_id) REFERENCES jobs(job_id),
	CONSTRAINT job_history_departments_department_id FOREIGN KEY (department_id) REFERENCES departments(department_id) */
	);

CREATE VIEW emp_details_view
AS
SELECT e.employee_id,
	e.job_id,
	e.manager_id,
	e.department_id,
	d.location_id,
	l.country_id,
	e.first_name,
	e.last_name,
	e.salary,
	e.commission_pct,
	d.department_name,
	j.job_title,
	l.city,
	l.state_province,
	c.country_name,
	r.region_name
FROM employees e,
	departments d,
	jobs j,
	locations l,
	countries c,
	regions r
WHERE e.department_id = d.department_id
	AND d.location_id = l.location_id
	AND l.country_id = c.country_id
	AND c.region_id = r.region_id
	AND j.job_id = e.job_id;	
	