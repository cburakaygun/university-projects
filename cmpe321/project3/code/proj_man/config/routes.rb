Rails.application.routes.draw do
  root 'home#index'

  get    '/login',   to: 'sessions#new'
  post   '/login',   to: 'sessions#create'
  delete '/logout',  to: 'sessions#destroy'

  resources :projects
  resources :tasks
  resources :employees
  resources :project_managers
  resources :project_assignments
  resources :task_assignments

  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
