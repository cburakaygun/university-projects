class SessionsController < ApplicationController
  skip_before_action :require_login
  before_action :redirect_to_home, if: :user_logged_in?, except: [:destroy]

  def new
  end

  def create
    user = User.find_by(username: params[:session][:username].downcase)
    if user && user.authenticate(params[:session][:password])
      log_in(user)
      redirect_to root_url
    else
      flash.now[:danger] = 'Invalid email/password combination'
      render 'new'
    end
  end

  def destroy
    log_out
    redirect_to root_url
  end

  private

  def redirect_to_home
    redirect_to root_url
  end
end
