class CreateTasks < ActiveRecord::Migration[5.1]
  def change
    create_table :tasks do |t|
      t.string :name, null: false
      t.date :start_date, null: false
      t.integer :total_days, null: false, default: 0
      t.references :project, foreign_key: true

      t.timestamps
    end
  end
end
