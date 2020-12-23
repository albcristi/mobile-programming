from typing import Optional

from ..models import ApplicationUser


class UserRepository:

    def get_user(self, user_name: str) -> Optional[ApplicationUser]:
        try:
            return ApplicationUser.objects.get(pk=user_name)
        except Exception:
            return None

    def add_user(self, user_name: str, password: str, full_name: str) -> bool:
        user = self.get_user(user_name)
        if user is not None:
            return False
        user = ApplicationUser(username=user_name,
                               password=password,
                               full_name=full_name)
        user.save()
        return True
