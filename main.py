'''
 # @ Author: Group 23
 # @ Create Time: 2024-10-09 04:13:05
 # @ Modified time: 2024-10-09 04:14:30
 # @ Description:
 '''

import time 
import os
import csv
import discord
from discord.ext import tasks

# Define the intents of the bot
intents = discord.Intents()
intents.message_content = True
intents.messages = True
intents.reactions = True
intents.guilds = True

# The client itself
sokobot = discord.Client(intents=intents)

# Some globals
SENT_RESULTS = []

# Event listener for boot-up
@sokobot.event 
async def on_ready():
    print('Sokobot is active.')

# Event listener for messages
@sokobot.event
async def on_message(message):
    
    # Don't process bot messages
    if message.author.bot:
        return;
    
    # Use this channel for updates
    if message.content == '!sokobot start':
        
        # Send notif
        await message.channel.send('This channel will now be used for updates.\n**Running tests...**')

        # Start the task
        update.start(message.channel)

@tasks.loop(seconds=5)
async def update(channel):
    
    # I hate modifying global state but here we fucking go
    global SENT_RESULTS
        
    # Read the output file
    with open('result_tests.csv', 'r') as results:
        
        # Read the results
        results = csv.reader(results)
        
        # Update the sent stuff
        for row in results:
            
            # Not sent yet
            if row not in SENT_RESULTS:
                
                # Malformatted result
                if len(row) < 6:
                    continue

                # Grab deets                
                name = row[0]
                success = ':white_check_mark:' if row[5] == 'true' else ':exclamation:'
                seconds = row[2]
                moves = row[3]
                crates = row[4]
                solution = row[6]
                
                # Send update 
                await channel.send('# {} **{}**\n{}, {} moves for {} crates\n||`{}`||\n'.format(success, name, seconds, moves, crates, solution), silent=True)
                
                # Update the sent messages
                SENT_RESULTS.append(row)

# LMAO Im just leaving this shit out in the open
sokobot.run('MTI5MzIyNDA5OTg1NzgyNTg1Mg.GNwWa5.uFoDb5f9ncKS_MrXBINm66f2h4dAhseeCVWI2U')    
