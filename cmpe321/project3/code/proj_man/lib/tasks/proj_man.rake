namespace :proj_man do
  desc 'Sets up database stored procedures for ProjMan'
  task setup_sp_and_trigger: :environment do
    puts 'Dropping existing Stored Procedures and Triggers...'
    ActiveRecord::Base.connection.execute("DROP PROCEDURE IF EXISTS complete_projects;")
    ActiveRecord::Base.connection.execute("DROP PROCEDURE IF EXISTS incomplete_projects;")
    ActiveRecord::Base.connection.execute("DROP TRIGGER IF EXISTS del_task_assignment;")
    ActiveRecord::Base.connection.execute("DROP TRIGGER IF EXISTS assign_least_pm;")


    # STORED PROCEDURE 1: complete_projects(pm_id)
    # Takes ProjectManager ID as parameter.
    # If parameter is ALL, returns all complete projects in the system.
    # Else, returns all complete projects of the ProjectManager with id = pm_id.
    #
    # A project is 'complete' if there are at least one task related to it and
    # all of its tasks are simply past-dated.
    ActiveRecord::Base.connection.execute <<-SQL
      CREATE DEFINER='root'@'localhost' PROCEDURE complete_projects (IN pm_id CHAR(20))
      BEGIN
        IF pm_id = 'ALL' THEN
          SELECT p.*
          FROM projects AS p
          WHERE EXISTS (SELECT *
                        FROM tasks
                        WHERE tasks.project_id = p.id)

                AND NOT EXISTS (SELECT *
                                FROM tasks AS t
                                WHERE t.project_id = p.id
                                      AND t.start_date + t.total_days > CURDATE());
        ELSE
          SELECT p.*
          FROM projects AS p, project_assignments AS pa
          WHERE (p.id = pa.project_id
                AND pa.project_manager_id = pm_id)
                AND (EXISTS (SELECT *
                            FROM tasks
                            WHERE tasks.project_id = p.id)
                AND NOT EXISTS (SELECT *
                                FROM tasks AS t
                                WHERE t.project_id = p.id
                                      AND t.start_date + t.total_days > CURDATE()));
        END IF;
      END
    SQL

    puts 'Stored Procedure (complete_projects) was created.'

    # STORED PROCEDURE 2: incomplete_projects(pm_id)
    # Takes ProjectManager ID as parameter.
    # If parameter is ALL, returns all incomplete projects in the system.
    # Else, returns all incomplete projects of the ProjectManager with id = pm_id.
    #
    # A project is 'incomplete' if there aren't any tasks related to it or
    # at least one of its tasks is not past-dated yet.
    ActiveRecord::Base.connection.execute <<-SQL
      CREATE DEFINER='root'@'localhost' PROCEDURE incomplete_projects (IN pm_id CHAR(20))
      BEGIN
        IF pm_id = 'ALL' THEN
          SELECT p.*
          FROM projects AS p
          WHERE NOT EXISTS (SELECT *
                            FROM tasks
                            WHERE tasks.project_id = p.id)

                OR EXISTS (SELECT *
                           FROM tasks AS t
                           WHERE t.project_id = p.id
                                 AND t.start_date + t.total_days > CURDATE());
        ELSE
          SELECT p.*
          FROM projects AS p, project_assignments AS pa
          WHERE (p.id = pa.project_id
                AND pa.project_manager_id = pm_id)
                AND (NOT EXISTS (SELECT *
                                FROM tasks
                                WHERE tasks.project_id = p.id)
                OR EXISTS (SELECT *
                           FROM tasks AS t
                           WHERE t.project_id = p.id
                                 AND t.start_date + t.total_days > CURDATE()));
        END IF;
      END
    SQL


    puts 'Stored Procedure (incomplete_projects) was created.'


    # TRIGGER 1: del_task_assignment
    # When an Employee is deleted,
    # this trigger will delete all the TaskAssignments of that Employee.
    #
    # I.e., when a record e from 'employees' table is deleted,
    # all the records in 'task_assignments' table with 'employee_id' = e.id
    # will also be deleted.
    ActiveRecord::Base.connection.execute <<-SQL
      CREATE DEFINER='root'@'localhost' TRIGGER del_task_assignment BEFORE DELETE ON employees
      FOR EACH ROW DELETE FROM task_assignments WHERE employee_id = OLD.id;
    SQL


    puts 'Trigger (del_task_assignment) was created.'


    # TRIGGER 2: assign_least_pm
    # When a Project is created,
    # this trigger will assign that Project to the ProjectManager with the
    # least Project assignments on him/her currently.
    #
    # I.e., when a record p is inserted into 'projects' table,
    # a record pa will be inserted into 'project_assignments' table
    # with 'project_id' = 'p.id' & 'project_manager_id' = <id of a record in 'project_managers' table>.
    ActiveRecord::Base.connection.execute <<-SQL
      CREATE DEFINER='root'@'localhost' TRIGGER assign_least_pm AFTER INSERT ON projects
      FOR EACH ROW
      BEGIN
      SELECT u.id into @pm_id
      FROM users AS u
      WHERE u.type = 'ProjectManager'
            AND NOT EXISTS (SELECT *
                            FROM project_assignments AS pa
                            WHERE pa.project_manager_id = u.id)
            LIMIT 1;

      IF @pm_id IS NOT NULL THEN
        INSERT INTO project_assignments(project_id, project_manager_id, created_at, updated_at)
                    VALUES (NEW.id, @pm_id, NOW(), NOW());
      ELSE
        SELECT result.pm_id into @pm_id2
            FROM (SELECT project_manager_id AS pm_id, COUNT(*) AS ct
                  FROM project_assignments
                  GROUP BY pm_id
                  ORDER BY ct ASC
                  LIMIT 1) AS result;

        IF @pm_id2 IS NOT NULL THEN
          INSERT INTO project_assignments(project_id, project_manager_id, created_at, updated_at)
                      VALUES (NEW.id, @pm_id2, NOW(), NOW());
        END IF;

      END IF;
      END
    SQL


    puts 'Trigger (assign_least_pm) was created.'


    ActiveRecord::Base.clear_active_connections!
  end

  desc 'Creates an Admin'
  task setup_admin: :environment do
    Admin.create!(username: 'admin', password: '1234', first_name: 'Admin', last_name: 'One')
    puts 'Admin created. username: admin, password: 1234, first_name: Admin, last_name: One'
  end


  desc 'Creates an Admin with params'
  task :create_admin, [:username, :password, :first_name, :last_name] => :environment do |task, args|
    Admin.create!(username: args[:username], password: args[:password], first_name: args[:first_name], last_name: args[:last_name])
    puts "Admin created. username: #{args[:username]}, password: #{args[:password]}, first_name: #{args[:first_name]}, last_name: #{args[:last_name]}"
  end

end
