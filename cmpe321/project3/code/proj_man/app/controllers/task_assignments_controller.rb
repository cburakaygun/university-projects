class TaskAssignmentsController < ApplicationController
  before_action :require_project_manager

  def create
    prms = task_assignment_params

    task_project_id = Task.find(prms[:task_id]).project_id
    project_ids = ProjectAssignment.where(project_manager_id: current_user.id).pluck(:project_id)
    unless project_ids.include?(task_project_id)
      flash[:danger] = "You cannot assign an Employee to a Task that doesn't belong to any of your Projects."
      redirect_to root_url
      return
    end

    src = prms.delete(:src)
    task_assignment = TaskAssignment.new(prms)

    if task_assignment.save
      flash[:success] = 'Task was assigned to the Employee.'
    else
      flash[:danger] = ["Task COULDN't be assigned to the Employee."]
      task_assignment.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end

    redirect_user(src, prms[:task_id], prms[:employee_id])
  end

  def destroy
    task_project_id = Task.find(params[:task_id]).project_id
    project_ids = ProjectAssignment.where(project_manager_id: current_user.id).pluck(:project_id)
    unless project_ids.include?(task_project_id)
      flash[:danger] = "You cannot unassign an Employee from a Task that doesn't belong to any of your Projects."
      redirect_to root_url
      return
    end

    TaskAssignment.find_by(task_id: params[:task_id],
                           employee_id: params[:employee_id]).destroy
    flash[:success] = 'Task assignment was deleted.'
    redirect_user(params[:src], params[:task_id], params[:employee_id])
  end

  private

  def redirect_user(src, task_id, employee_id)
    case src
    when 'task'
      redirect_to task_url(task_id)
    when 'employee'
      redirect_to employee_url(employee_id)
    else
      redirect_to root_url
    end
  end

  def task_assignment_params
    params.require(:task_assignment).permit(:task_id, :employee_id, :src)
  end

end