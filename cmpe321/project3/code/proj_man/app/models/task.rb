class Task < ApplicationRecord
  belongs_to :project
  has_many :task_assignments, dependent: :destroy
  has_many :employees, through: :task_assignments

  validates :name, presence: true,
                   length: { minimum: 4, maximum: 256 },
                   format: { with: /\A[a-zA-Z0-9 ]+\z/,
                             message: 'should contain only letters, digits and/or space' }


  before_save do
    self.name = name.squish
  end
end