from django.contrib import admin
from .models import ApplicationUser, UserEvent
# Register your models here.


admin.site.register(ApplicationUser)
admin.site.register(UserEvent)
