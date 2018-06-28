class ProjectAssignmentsController < ApplicationController
  before_action :require_admin

  def create
    prms = project_assingment_params
    src = prms.delete(:src)
    project_assignment = ProjectAssignment.new(prms)

    if project_assignment.save
      flash[:success] = 'Project was assigned to the Project Manager.'
    else
      flash[:danger] = ["Project COULDN't be assigned to the Project Manager."]
      project_assignment.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end

    redirect_user(src, prms[:project_id], prms[:project_manager_id])
  end

  def destroy
    ProjectAssignment.find_by(project_id: params[:project_id],
                              project_manager_id: params[:project_manager_id]).destroy
    flash[:success] = 'Project assignment was deleted.'
    redirect_user(params[:src], params[:project_id], params[:project_manager_id])
  end

  private

  def redirect_user(src, project_id, project_manager_id)
    case src
    when 'project'
      redirect_to project_url(project_id)
    when 'project_manager'
      redirect_to project_manager_url(project_manager_id)
    else
      redirect_to root_url
    end
  end

  def project_assingment_params
    params.require(:project_assignment).permit(:project_id, :project_manager_id, :src)
  end

end