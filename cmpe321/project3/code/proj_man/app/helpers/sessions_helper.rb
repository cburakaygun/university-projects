module SessionsHelper
  # Logs in the given user.
  def log_in(user)
    session[:user_id] = user.id
  end

  # Logs out the current user.
  def log_out
    session.delete(:user_id)
    @current_user = nil
  end

  # Returns the current logged-in user (if any).
  def current_user
    return nil unless session[:user_id]
    @current_user ||= User.find_by(id: session[:user_id])
  end

  # Returns true if the user is logged in, false otherwise.
  def user_logged_in?
    !current_user.nil?
  end

  # Returns true if the user is logged in and is an Admin, false otherwise.
  def user_admin?
    user_logged_in? && @current_user.type == 'Admin'
  end

  # Returns true if the user is logged in and is a Project Manager, false otherwise.
  def user_project_manager?
    user_logged_in? && current_user.type == 'ProjectManager'
  end

  def require_login
    return if user_logged_in?
    flash[:warning] = 'You need to log in to use the system.'
    redirect_to login_url
  end

  def require_admin
    return if user_admin?
    flash[:warning] = 'You need to be an Admin.'
    redirect_to root_url
  end

  def require_project_manager
    return if user_project_manager?
    flash[:warning] = 'You need to be a ProjectManager.'
    redirect_to root_url
  end
end
