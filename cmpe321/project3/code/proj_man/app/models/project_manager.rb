class ProjectManager < User
  has_many :project_assignments, dependent: :destroy
  has_many :projects, through: :project_assignments
end
