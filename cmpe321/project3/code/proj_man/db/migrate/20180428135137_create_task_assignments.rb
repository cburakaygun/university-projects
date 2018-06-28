class CreateTaskAssignments < ActiveRecord::Migration[5.1]
  def change
    create_table :task_assignments do |t|
      t.references :task, foreign_key: true
      t.references :employee, foreign_key: true

      t.timestamps
    end
  end
end
