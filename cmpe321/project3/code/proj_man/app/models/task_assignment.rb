class TaskAssignment < ApplicationRecord
  belongs_to :task
  belongs_to :employee

  validates_uniqueness_of :task_id, scope: :employee_id
  before_save :check_employee_status

  private

  # Checks if the Employee with :employee_id has an conflicting Task
  # with the Task with :task_id before assignment.
  def check_employee_status
    task = Task.find(task_id)
    employee = Employee.find(employee_id)
    employee.tasks.each do |t|
      if tasks_conflict?(task, t)
        errors.add(:base, 'The Employee has an conflicting Task.')
        throw :abort
      end
    end
  end

  # Returns true if Tasks t1 & t2 conflicts. Else, returns false.
  # 2 Tasks conflict if they have at least one day in common.
  def tasks_conflict?(t1, t2)
    t1_end_date = t1.start_date + t1.total_days
    t2_end_date = t2.start_date + t2.total_days

    !(t1_end_date < t2.start_date || t2_end_date < t1.start_date)
  end
end