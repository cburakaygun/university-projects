class ChangeColumnUserIdInProjectAssignments < ActiveRecord::Migration[5.1]
  def change
    rename_column :project_assignments, :user_id, :project_manager_id
  end
end
