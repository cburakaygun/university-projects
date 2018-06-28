class Employee < ApplicationRecord
  has_many :task_assignments # dependent: :destroy is implemented in DB as a TRIGGER.
  has_many :tasks, through: :task_assignments

  validates :first_name, :last_name, presence: true,
                                     length: { minimum: 2, maximum: 16 }

  validates :first_name, format: { with: /\A[a-zA-Z]+ ?[a-zA-Z]+\z/,
                                   message: 'should contain only letters and/or space' }

  validates :last_name, format: { with: /\A[a-zA-Z]+\z/,
                                  message: 'should contain only letters' }

  before_save do
    self.first_name = first_name.squish
    self.last_name = last_name.squish
  end
end