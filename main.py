'''
 # @ Author: Group 23
 # @ Create Time: 2024-10-09 04:13:05
 # @ Modified time: 2024-10-09 04:14:30
 # @ Description:
 '''

import datetime
import os
import csv
import numpy as np
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
UPDATE_MESSAGE_ID = None
RESULT_PROCESSED = []
RESULT_MAP_PER_CRATE = {}
RESULT_MESSAGE_TEMPLATE = '''
# TEST SUMMARY

> `2 crates {}`
> `3 crates {}`
> `4 crates {}`
> `5 crates {}`
> `6 crates {}`
> `7 crates {}`
> `8 crates {}`
> `9 crates {}`
> `10 crates {}`
> `11 crates {}`
> `12 crates {}`
> `13 crates {}`
> `14 crates {}`
> `15 crates {}`
> `16 crates {}`
'''

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
        await message.channel.send('This channel will now be used for updates.')

        # Start the task
        update.start(message.channel)

@tasks.loop(seconds=7.5)
async def update(channel):
    
    # I hate modifying global state but here we fucking go
    global UPDATE_MESSAGE_ID
    global RESULT_PROCESSED
    global RESULT_MAP_PER_CRATE
    global RESULT_MESSAGE_TEMPLATE
    
    # Send a message to update first
    if UPDATE_MESSAGE_ID == None:
        
        # Send a message
        message = await channel.send('# TEST SUMMARY', silent=True)
        UPDATE_MESSAGE_ID = message.id

    # Read the output file
    with open('result_tests.csv', 'r') as results:
        
        # Read the results
        results = csv.reader(results)
        
        # Update the sent stuff
        for row in results:
            
            # Skip first row
            if row[0] == 'test_name' and row[1] == 'test_file':
                continue
            
            # Not sent yet
            if row not in RESULT_PROCESSED:
                
                # Malformatted result
                if len(row) < 6:
                    continue
                
                # Grab the index property
                crate_count = row[4]
                
                # Mapping
                if crate_count not in RESULT_MAP_PER_CRATE:
                    RESULT_MAP_PER_CRATE[crate_count] = {
                        'times': [],
                        'moves': [],
                        'wins': 0,
                        'total': 0,
                    }
                    
                # Update map
                RESULT_MAP_PER_CRATE[crate_count]['total'] += 1
                if row[5] == 'true':
                    RESULT_MAP_PER_CRATE[crate_count]['times'].append(float(row[2].split('s')[0]))
                    RESULT_MAP_PER_CRATE[crate_count]['moves'].append(int(row[3]))
                    RESULT_MAP_PER_CRATE[crate_count]['wins'] += 1
                
                # Update the sent messages
                RESULT_PROCESSED.append(row)
            
        # Send update 
        rows = {}
        crate_counts = [ int(i) for i in RESULT_MAP_PER_CRATE ]
        crate_counts.sort()
        
        # Create display strings
        for crate_count in crate_counts:
            
            # Grab the data
            c = str(crate_count)
            data = RESULT_MAP_PER_CRATE[c]
            times = data['times']
            moves = data['moves']
            count = data['total']
            success = data['wins'] / data['total']
            
            t = times if len(times) else [ 0 ]
            m = moves if len(moves) else [ 0 ]
            
            # Yes
            rows[c] = '' 
            rows[c] += '{0:2s} - {1:d}\n'.format(c, count)
            rows[c] += '+   ' if success >= 0.5 else '-   '
            rows[c] += '[{}]'.format(''.join([ '#' if i < 12 * success else '-' for i in range(12)]))
            rows[c] += ' {0:6.2f}%\n'.format(success * 100)
            rows[c] += '--- times: {1:5.2f} ({2:5.2f} - {3:5.2f})\n'.format('', np.median(t), np.min(t), np.max(t))
            rows[c] += '--- moves: {1:5.1f} ({2:5d} - {3:5d})\n'.format('', np.median(m), np.min(m), np.max(m))

        # Timestamp
        timestamp = datetime.datetime.now()
        timestamp = timestamp.strftime('%H:%M:%S of %m/%d/%Y')

        # Create message
        message_content = '# TEST RESULTS\n'
        message_content = '```md\ncrate count - test count\n--- times: median (min, max)\n--- moves: median (min, max)```\n```diff\n'
        for row in rows:
            message_content += rows[row] + '\n'
        message_content += '```\n\n'
        message_content += '***Tests ran:** {}*\n'.format(len(RESULT_PROCESSED))
        message_content += '***Last update:** {}*\n'.format(timestamp)
        
        (print(len(message_content)))
            
        message = await channel.fetch_message(UPDATE_MESSAGE_ID)
        await message.edit(content=message_content)

# LMAO Im just leaving this shit out in the open
sokobot.run('MTI5MzIyNDA5OTg1NzgyNTg1Mg.GNwWa5.uFoDb5f9ncKS_MrXBINm66f2h4dAhseeCVWI2U')    
