* Make sure you have **Ruby**, **Rails** and **MySQL** installed. (Note that this project was implemented using `Ruby 2.2.2` and `Rails 5.1.4`. Other version may cause problems.) 

* Put your database server configs and credentials into `proj_man/config/database.yml` file.

* While in the directory `proj_man`, run the following commands on a `Terminal` in the following order: (ignore the parenthesis)

  1. `bundle install` (installs necessary packages (gems))

  2. `rails db:create` (creates an empty database named **proj_man_db**)

  3. `rails db:schema:load` (sets up database with relations, indexes, etc.)

  4. `rails proj_man:setup_sp_and_trigger` (creates stored procedures and creates in the database)


### Creating Admins

While in the directory `proj_man`, execute the following command on a `Terminal`:

`rails proj_man:setup_admin`

This command creates an Admin with username=`admin`, password=`1234`, first_name=`Admin`, last_name=`One`

***

You can create additional Admins using the following command:

`rails 'proj_man:create_admin[<username>, <password>, <first\_name>, <last\_name>]'`

This command creates an Admin with given parameters.

For example, following command will create an Admin with username=`admin2`, password=`1234`, first_name=`Admin`, last_name=`Two`

`rails 'proj_man:create_admin[admin2, 1234, Admin, Two]'`


### Starting ProjMan (Server)

While in the directory `proj_man`, execute the following command on a `Terminal`:

`rails s`

You will see on the `Terminal` the information for `IP:PORT` on which ProjMan should be active. (In general, it is `0.0.0.0:3000`)
