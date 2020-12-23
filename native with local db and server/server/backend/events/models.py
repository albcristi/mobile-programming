from django.db import models

# Create your models here.


class ApplicationUser(models.Model):
    class Meta:
        db_table = 'ApplicationUser'

    '''
        This class represents the accounts for the app
    '''
    username = models.CharField(max_length=200, primary_key=True)
    password = models.CharField(max_length=200)
    full_name = models.CharField(max_length=200)


class UserEvent(models.Model):
    class Meta:
        db_table = 'UserEvent'
    event_id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=300, default='')
    host = models.ForeignKey(ApplicationUser,
                             related_name='user_events',
                             on_delete=models.CASCADE)
    city = models.CharField(max_length=300, default="unknown")
    long = models.FloatField(default=0.0)
    lat = models.FloatField(default=0.0)
    start_hour = models.CharField(max_length=50)
    start_date = models.CharField(max_length=100)
    end_date = models.CharField(max_length=100)
    description = models.TextField()
