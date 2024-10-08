'''
 # @ Author: Group 23
 # @ Create Time: 2024-10-09 04:13:05
 # @ Modified time: 2024-10-09 04:14:30
 # @ Description:
 '''

import os
import discord

# Define the intents of the bot
intents = discord.Intents()
intents.message_content = True
intents.messages = True
intents.reactions = True
intents.guilds = True

# The client itself
sokobot = discord.Client(intents=intents)

@sokobot.event 
async def on_ready():
    print('Sokobot is active.')

@sokobot.event
async def on_message(message):
    print(message)
    

# LMAO Im just leaving this shit out in the open
sokobot.run('MTI5MzIyNDA5OTg1NzgyNTg1Mg.GNwWa5.uFoDb5f9ncKS_MrXBINm66f2h4dAhseeCVWI2U')