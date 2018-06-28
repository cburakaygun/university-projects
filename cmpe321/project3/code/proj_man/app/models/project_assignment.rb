class ProjectAssignment < ApplicationRecord
  belongs_to :project
  belongs_to :project_manager

  validates_uniqueness_of :project_manager_id, scope: :project_id
end