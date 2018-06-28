class TasksController < ApplicationController
  before_action :require_project_manager, only: [:create, :update]
  before_action :correct_user, if: :user_project_manager?, only: [:show, :update, :destroy]

  def create
    prms = task_params

    project_id = prms[:project_id]
    project_ids = ProjectAssignment.where(project_manager_id: current_user.id).pluck(:project_id)
    unless project_ids.include?(project_id)
      flash[:danger] = "You cannot create a Task for a Project that doesn't belong to you."
      redirect_to root_url
      return
    end

    task = Task.new(prms)
    if task.save
      flash[:success] = 'Task was created.'
    else
      flash[:danger] = "Task COULDN'T be created."
    end
    redirect_to project_url(project_id)
  end

  def show
    @task ||= Task.find(params[:id])
    @employees = @task.employees.all
    @project ||= @task.project
    @task_assignment = TaskAssignment.new
  end

  def index
    @tasks = if user_admin?
               Task.all
             elsif user_project_manager?
               project_ids = ProjectAssignment.where(project_manager_id: current_user.id).pluck(:project_id)
               Task.where(project_id: project_ids)
             end
  end

  def update
    @task ||= Task.find(params[:id])
    if params[:employee]
      assign_employee(params[:employee][:id])

    elsif @task.update(task_params)
      flash[:success] = 'Task was updated.'
    else
      flash[:danger] = ["Task COULDN'T be updated."]
      @task.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
    redirect_to @task
  end

  def destroy
    Task.find(params[:id]).destroy
    flash[:success] = 'Task was deleted.'
    redirect_to tasks_url
  end

  private

  def task_params
    prms = params.require(:task).permit(:name, :start_date, :total_days)
    start_date_y = prms['start_date(1i)']
    start_date_m = prms['start_date(2i)']
    start_date_d = prms['start_date(3i)']
    prms[:start_date] = Date.new(start_date_y.to_i, start_date_m.to_i, start_date_d.to_i)
    prms
  end

  def assign_employee(employee_id)
    task_assignment = TaskAssignment.new(employee_id: employee_id,
                                         task_id: @task.id)
    if task_assignment.save
      flash[:success] = 'Task was assigned to the Employee.'
    else
      flash[:danger] = ["Task COULDN'T be assigned to the Employee."]
      task_assignment.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
  end

  def correct_user
    @task = Task.find(params[:id])
    @project = @task.project
    pm_ids = ProjectAssignment.where(project_id: @project.id).pluck(:project_manager_id)

    return if pm_ids.include?(current_user.id)

    flash[:warning] = "You cannot reach to a Task that doesn't belong to any of your Projects."
    redirect_to(root_url)
  end

end