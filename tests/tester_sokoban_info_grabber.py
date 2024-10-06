'''
 # @ Author: Group 23
 # @ Create Time: 2024-10-06 14:58:44
 # @ Modified time: 2024-10-06 15:00:14
 # @ Description:
 
 This file grabs sokoban maps from https://sokoban.info
 '''

from bs4 import BeautifulSoup
import os
import re
import requests

# These are where the maps will be from
HOME = 'https://sokoban.info/'

# How many maps per group
MAP_GROUPS = {
    '2': 108,
    '3': 120,
    '4': 40,
    '5': 40,
    '6': 20,
    '7': 20,
    '8': 10,
    '9': 10,
    '10': 20,
    '11': 12,
    '12': 20,
    '13': 13,
    '14': 21,
    '15': 50,
    '16': 50, 
}

def generate_map_url(group_number, map_number, home=HOME):
    """Simply generates a valid url for a map

    Args:
        group_number (int): The group to get the map from. Check site for more info.
        map_number (int): The map from the set to obtain.
        home (string, optional): The domain of the url. Defaults to HOME.

    Returns:
        string: A valid url for the specified map.
    """
    
    return '{}?{}_{}'.format(home, group_number, map_number)    

def get_parsed_html(url):
    """Grabs the parsed html from the provided url.

    Args:
        url (string): The url to grab.

    Returns:
        HTMLObject: The parsed html tree.
    """
    
    response = requests.get(url)
    return BeautifulSoup(response.text, features='lxml')

def get_title(html):
    """Retrieves the title of the page from the html.

    Args:
        html (string): The title of the page.
    """

    return html.head.find('title').text.replace('-', '').replace(' ', '-').replace('--', '-').replace('#', '')

def get_board(html):
    """Retrieves the value of the board variable from the html.
    Looks for the specific location in the scripts of the head of the html.

    Args:
        html (HTMLObject): A beautiful soup instance.

    Returns:
        string: The actual board present in the html.
    """

    # The text to look for in the script
    board_variable = 'var Board'

    # Get all script tags in the head
    for script in html.head.findAll('script'):
        
        # The line in the JS code containing the board init
        if re.search(board_variable, script.text):
            
            # Grab the value assigned to the board
            board_value = script.text.split(board_variable, 1)[-1].rsplit(';')[0]
            board_string = board_value.strip(' ="\t').replace('!', '\n').replace('x', '#')
            
            # Return the final board string
            return board_string
        
def save_board(filename, board):
    """Saves the provided board into a text file with the given name.

    Args:
        filename (string): The name of the file to save.
        board (string): A string containing the sokoban board information.
    """
    
    # Write the file to disk
    file = open('{}.txt'.format(filename), 'w')
    file.write(board)
    file.close()
    
def retrieve_boards(home=HOME, map_groups=MAP_GROUPS, out=''):
    """Retrieves all the boards and saves them appropriately.
    Keeps track of the finished files too.

    Args:
        home (string, optional): The home url to use. Defaults to HOME.
        map_groups (string, optional): The group and group size info. Defaults to MAP_GROUPS.
        out (str, optional): The output folder. Defaults to ''.
    """
    
    # So we don't repeat requests when re-running
    finished_file = open(os.path.join(out, 'done.txt'), 'r+')
    finished_maps = [ line.split('_')[0] for line in finished_file.readlines() ]
    
    # Grab the different groups
    for map_group in map_groups.keys():
        
        # Number of maps in group
        map_group_size = map_groups[map_group]
        
        # Retrieve board
        for map_number in range(1, map_group_size + 1):
            
            # Check if file exists already
            if '{}-{}'.format(map_group, map_number) in finished_maps:
                continue
            
            # Grab the page
            map_url = generate_map_url(map_group, map_number)
            map_html = get_parsed_html(map_url)
            map_html_title = get_title(map_html)
            
            # Isolate and save the board
            board_name = '{}-{}_{}'.format(map_group, map_number, map_html_title)
            board = get_board(map_html)
            save_board(os.path.join(out, board_name), board)
            
            # Update finished_maps
            finished_file.write(board_name)
            finished_file.write('\n')
            finished_file.flush()
            
    # Close the file
    finished_file.close()
    
if __name__ == '__main__':
    retrieve_boards(out='./maps/sokoban-info')

