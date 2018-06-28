class ProjectManagersController < UsersController
  before_action :require_admin, except: [:show, :update]
  before_action :correct_user, if: :user_project_manager?, only: [:show, :update]

  def new
    @project_manager = ProjectManager.new
  end

  def create
    @project_manager = ProjectManager.new(project_manager_params)
    if @project_manager.save
      flash[:success] = 'ProjectManager was created.'
      redirect_to @project_manager
    else
      flash.now[:danger] = "ProjectManager COULDN'T be created."
      render 'new'
    end
  end

  def show
    @project_manager ||= ProjectManager.find(params[:id])
    @projects = @project_manager.projects.all
    @project_assignment = ProjectAssignment.new
  end

  def index
    @project_managers = ProjectManager.all
  end

  def update
    @project_manager ||= ProjectManager.find(params[:id])
    if params[:project]
      assign_project

    elsif @project_manager.update(project_manager_params)
      flash[:success] = 'ProjectManager was updated.'

    else
      flash[:danger] = ["ProjectManager COULDN'T be updated."]
      @project_manager.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
    redirect_to @project_manager
  end

  def destroy
    ProjectManager.find(params[:id]).destroy
    flash[:success] = 'Project Manager was deleted.'
    redirect_to project_managers_url
  end

  private

  def project_manager_params
    params.require(:project_manager).permit(:username, :password, :password_confirmation, :first_name, :last_name)
  end

  def assign_project
    project_assignment = ProjectAssignment.new(project_manager_id: @project_manager.id,
                                               project_id: params[:project][:id])
    if project_assignment.save
      flash[:success] = 'Project was assigned to the ProjectManager.'
    else
      flash[:danger] = ["Project COULDN'T be assigned to the ProjectManager."]
      project_assignment.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
  end

  def correct_user
    @project_manager = ProjectManager.find(params[:id])
    return if @project_manager == current_user

    flash[:warning] = ["You need to be an admin to view another Project Manager's page."]
    redirect_to root_url
  end

end