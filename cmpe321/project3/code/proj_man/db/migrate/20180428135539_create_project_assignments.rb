class CreateProjectAssignments < ActiveRecord::Migration[5.1]
  def change
    create_table :project_assignments do |t|
      t.references :project, foreign_key: true
      t.references :user, foreign_key: true

      t.timestamps
    end
  end
end
