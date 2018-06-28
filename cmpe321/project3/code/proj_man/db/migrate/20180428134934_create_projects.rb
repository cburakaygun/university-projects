class CreateProjects < ActiveRecord::Migration[5.1]
  def change
    create_table :projects do |t|
      t.string :name, null: false
      t.date :start_date, null: false
      t.integer :task_days, null: false, default: 0

      t.timestamps
    end
  end
end
