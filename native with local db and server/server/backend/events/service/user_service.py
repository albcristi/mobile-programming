from typing import Optional

from ..repository.user_repository import UserRepository
from ..models import ApplicationUser


class UserService:
    def __init__(self):
        self.__repository = UserRepository()

    def do_log_in(self, username: str, password: str) -> bool:
        user = self.__repository.get_user(username)
        if user is None:
            return False
        return user.password == password

    def add_user(self, username: str, password: str, full_name: str) -> bool:
        return self.__repository.add_user(username, password, full_name)

    def username_is_unique(self, username: str) -> bool:
        if self.__repository.get_user(username) is None:
            return True
        return False

    def get_user(self, username: str) -> Optional[ApplicationUser]:
        return self.__repository.get_user(username)
