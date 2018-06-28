class User < ApplicationRecord
  validates :username, :first_name, :last_name, presence: true,
                                                length: { minimum: 2, maximum: 16 }

  validates :username, uniqueness: { case_sensitive: false },
                       format: { with: /\A[\p{L}0-9]+\z/i,
                                 message: 'should contain only letters and/or digits' }

  validates :first_name, format: { with: /\A[\p{L}]+ ?[\p{L}]+\z/i,
                                   message: 'should contain only letters and/or space' }

  validates :last_name, format: { with: /\A[\p{L}]+\z/i,
                                  message: 'should contain only letters' }

  has_secure_password
  validates :password, presence: true, length: { minimum: 4, maximum: 16 }

  before_save do
    self.username = username.squish
    self.first_name = first_name.squish
    self.last_name = last_name.squish
  end
end