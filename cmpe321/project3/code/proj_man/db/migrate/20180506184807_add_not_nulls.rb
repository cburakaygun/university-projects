class AddNotNulls < ActiveRecord::Migration[5.1]
  def change
    change_column_null :project_assignments, :project_id, false
    change_column_null :project_assignments, :project_manager_id, false
    change_column_null :task_assignments, :task_id, false
    change_column_null :task_assignments, :employee_id, false
    change_column_null :tasks, :project_id, false
  end
end
