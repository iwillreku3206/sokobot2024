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
    '1': 90,
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
    '17': 50,
    '18': 50,
    '19': 50,
    '20': 50,
    '21': 50,
    '22': 50,
    '23': 50,
    '24': 33,
    '25': 155,
    '26': 135,
    '27': 101,
    '28': 25,
    '29': 54,
    '30': 52,
    '31': 50,
    '32': 50,
    '33': 50,
    '34': 50,
    '35': 10,
    '36': 12,
    '37': 46,
    '38': 40,
    '39': 47,
    '40': 40,
    '41': 40,
    '42': 40,
    '43': 40,
    '44': 100,
    '45': 100,
    '46': 36,
    '47': 100,
    '48': 50,
    '49': 100,
    '50': 75,
    '51': 55,
    '52': 40,
    '53': 61,
    '54': 50,
    '55': 50,
    '56': 100,
    '57': 40,
    '58': 26,
    '59': 40,
    '60': 40,
    '61': 40,
    '62': 40,
    '63': 100,
    '64': 100,
    '71': 8,
    '76': 44,
    '77': 50,
    '78': 50,
    '94': 25,
    '96': 30,
    '97': 25,
    '98': 10,
    '100': 50,
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
    finished_file = open(os.path.join(out, '.done.txt'), 'r+')
    finished_maps = [ line.split('_')[0] for line in finished_file.readlines() ]
    
    # Grab the different groups
    for map_group in map_groups.keys():
        
        # Number of maps in group
        map_group_size = map_groups[map_group]
        
        # Retrieve board
        for map_number in range(1, map_group_size + 1):
            
            name = '{}-{}'.format(map_group, map_number)
            
            # Check if file exists already
            if name in finished_maps:
                print('[!]  skipping ' + name)
                continue
            
            # Status update
            print('[/]  retrieving {} of {}...'.format(name, map_group_size + 1))
            
            # Grab the page
            map_url = generate_map_url(map_group, map_number)
            map_html = get_parsed_html(map_url)
            map_html_title = get_title(map_html).replace('&', 'and')
            
            # Isolate and save the board
            board_name = '{}-{}_{}'.format(map_group, map_number, map_html_title)
            board = get_board(map_html)
            
            # Skip boards w too many crates
            if board.count('$') + board.count('*') > 16:
                print('(x)\tboard was too big')
                
                # It was already attempted too
                finished_file.write(board_name)
                finished_file.write('\n')
                finished_file.flush()
                
                continue;
            
            save_board(os.path.join(out, board_name), board)
            
            # Update finished_maps
            finished_file.write(board_name)
            finished_file.write('\n')
            finished_file.flush()
            
            print('(/)\tsaved.')
            
    # Close the file
    finished_file.close()
    
if __name__ == '__main__':
    retrieve_boards(out='./maps/sokoban-info')

