class ProjectsController < ApplicationController
  before_action :require_admin, except: [:show, :index, :update]
  before_action :correct_user, if: :user_project_manager?, only: [:show, :update]

  def new
    @project = Project.new
  end

  def create
    @project = Project.new(project_params)
    if @project.save
      flash[:success] = 'Project was created.'
      redirect_to @project
    else
      flash.now[:danger] = "Project COULDN'T BE created."
      render 'new'
    end
  end

  def index
    params[:stat] = :all.to_s if params[:stat].blank?

    if user_admin?
      @projects = if params[:stat] == :all.to_s
                    if params[:pm_id].blank?
                      Project.all
                    else
                      project_ids = ProjectAssignment.where(project_manager_id: params[:pm_id].to_i).pluck(:project_id)
                      Project.where(id: project_ids)
                    end
                  else
                    params[:pm_id] = 'ALL' if params[:pm_id].blank?
                    projects_by_stat_pm_id(params[:stat].to_sym, params[:pm_id])
                  end

    elsif user_project_manager?
      @projects = if params[:stat] == :all.to_s
                    project_ids = ProjectAssignment.where(project_manager_id: current_user.id).pluck(:project_id)
                    Project.where(id: project_ids)
                  else
                    projects_by_stat_pm_id(params[:stat].to_sym, current_user.id.to_s)
                  end
    end
  end

  def show
    @project = Project.find(params[:id])
    @tasks = @project.tasks.all
    @project_assignment = ProjectAssignment.new
    @project_managers = @project.project_managers.all
  end

  def update
    return if params[:task].nil? && user_project_manager?

    @project = Project.find(params[:id])
    if params[:task]
      create_task

    elsif params[:project_manager]
      assign_project_manager

    elsif @project.update(project_params)
      flash[:success] = 'Project was updated.'
    else
      flash[:danger] = ["Project COULDN'T be updated."]
      @project.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
    redirect_to @project
  end

  def destroy
    Project.find(params[:id]).destroy
    flash[:success] = 'Project was deleted.'
    redirect_to projects_url
  end

  private

  def project_params
    prms = params.require(:project).permit(:name, :start_date, :task_days)
    start_date_y = prms['start_date(1i)']
    start_date_m = prms['start_date(2i)']
    start_date_d = prms['start_date(3i)']
    prms[:start_date] = Date.new(start_date_y.to_i, start_date_m.to_i, start_date_d.to_i)
    prms
  end

  def create_task
    task_hash = params[:task]
    task = Task.new(project_id: @project.id,
                    name: task_hash[:name].html_safe,
                    start_date: Date.new(task_hash['start_date(1i)'].to_i, task_hash['start_date(2i)'].to_i, task_hash['start_date(3i)'].to_i),
                    total_days: task_hash[:total_days])
    if task.save
      flash[:success] = 'Task was created.'
    else
      flash[:danger] = ['Task was NOT created.']
      task.errors.full_messages.each do |m|
        flash[:danger] << m
      end

    end
  end

  def assign_project_manager
    project_assignment = ProjectAssignment.new(project_id: @project.id,
                                               project_manager_id: params[:project_manager][:id])
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
    pm_ids = ProjectAssignment.where(project_id: params[:id]).pluck(:project_manager_id)

    return if pm_ids.include?(current_user.id)

    flash[:warning] = 'You are not assigned to this project.'
    redirect_to(root_url)
  end

  def projects_by_stat_pm_id(stat, pm_id)
    case stat
    when :complete
      res = ActiveRecord::Base.connection.exec_query("CALL complete_projects('#{pm_id}');")
    else
      res = ActiveRecord::Base.connection.exec_query("CALL incomplete_projects('#{pm_id}');")
    end
    ActiveRecord::Base.clear_active_connections!
    res.entries.map { |p| Project.new(p) }
  end

end