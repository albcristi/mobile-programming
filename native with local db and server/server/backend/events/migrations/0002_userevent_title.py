# Generated by Django 3.1 on 2020-12-22 15:42

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('events', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='userevent',
            name='title',
            field=models.CharField(default='', max_length=300),
        ),
    ]