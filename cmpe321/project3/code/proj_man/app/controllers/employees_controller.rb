class EmployeesController < ApplicationController
  before_action :require_admin

  def new
    @employee = Employee.new
  end

  def create
    @employee = Employee.new(employee_params)
    if @employee.save
      flash[:success] = 'Employee was created.'
      redirect_to @employee
    else
      flash.now[:danger] = "Employee COULDN'T be created."
      render 'new'
    end
  end

  def show
    @employee = Employee.find(params[:id])
    @tasks = @employee.tasks.all
    @task_assignment = TaskAssignment.new
  end

  def index
    @employees = Employee.all
  end

  def update
    @employee = Employee.find(params[:id])
    if params[:task]
      assign_task

    elsif @employee.update(employee_params)
      flash[:success] = 'Employee was updated.'
    else
      flash[:danger] = ["Employee COULDN'T be updated."]
      @employee.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
    redirect_to @employee
  end

  def destroy
    Employee.find(params[:id]).destroy
    flash[:success] = 'Employee was deleted.'
    redirect_to employees_url
  end

  private

  def employee_params
    params.require(:employee).permit(:first_name, :last_name)
  end

  def assign_task
    task_assignment = TaskAssignment.new(employee_id: @employee.id,
                                         task_id: params[:task][:id])
    if task_assignment.save
      flash[:success] = 'Task was assigned to the Employee.'
    else
      flash[:danger] = ["Task COULDN'T be assigned to the Employee."]
      task_assignment.errors.full_messages.each do |m|
        flash[:danger] << m
      end
    end
  end

end
