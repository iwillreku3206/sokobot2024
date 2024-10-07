'''
 # @ Author: Group 23
 # @ Create Time: 2024-10-05 12:24:49
 # @ Modified time: 2024-10-05 12:55:07
 # @ Description:
 
 Automates running the tests for us.
 Initially, I thought of creating a test driver in Java, but that ended up lagging.
 This is neater anyway, and provides even better isolation among tests because they execute in processes.
 '''

import os
import time
import datetime
import csv
import re
from sys import platform

IS_CSV = True
OUT_FILE = 'results.txt'
TIMESTAMP_FORMAT = '%H:%M:%S %d/%m/%Y'
MAP_FOLDER = 'maps'
MAP_NAMES = []

def get_timestamp(format: str =TIMESTAMP_FORMAT) -> str:
    """Returns the timestamp at a given point in time.
    Automatically formats the timestamp according to the template provided.

    Args:
        format (string, optional): The format to use for the timestamp. Defaults to TIMESTAMP_FORMAT.

    Returns:
        string: A formatted string representing the current time.
    """
    
    ts = time.time()
    dt = datetime.datetime.fromtimestamp(ts).strftime(format)
    return dt

def get_maps(folder=MAP_FOLDER):
    """Grabs all the map names in the given folder.
    Specifies the map name as a relative path from the map folder root.

    Args:
        folder (string, optional): The folder to look for map names. Defaults to MAP_FOLDER.

    Returns:
        list: A list of the names of the maps seen.
    """
    
    # The map names
    map_names = []
    
    # Iterative traversal 
    for root, dirs, files in os.walk(folder):
        for file in files:
            
            # Not a map
            if '.done' in file:
                continue
            
            # Append the file name
            fullpath = os.path.join(root, file)
            relpath = os.path.relpath(fullpath, folder)
            map_names.append(relpath.split('.')[0])
            
    # Return map names
    return map_names

def process_to_csv(test_name: str, map_name: str) -> None:
    """Process output of tests and stores into csv.

    Args:
        test_name (string): The name of the test.
        map_name (string): The name of the map.
    """
    with open('result_tests.csv', 'a', newline='') as csvfile:
        fieldnames: list[str] = ['test_name', 'test_file', 'time_taken', 'no_move', 'no_c', 'has_won', 'solution'];
        writer = csv.writer(csvfile)
        
        # Do the test
        start = get_timestamp()
        out_text = os.popen("java -classpath out tests.Tester {} {}".format(test_name, map_name)).read()
        end = get_timestamp()
        
        # Process information per row
        test_info: list[str] = re.split(r'\n', out_text);
        row_info = [];
        
        for test_data in test_info:
            # Split (Data_name : Data_value) into array
            test_data = test_data.split(":")
            test_data = [t.strip() for t in test_data];
            
            # Get rid of \n
            if len(test_data) == 2:
                #append Data_value
                row_info.append(test_data[1]) 
                
        writer.writerow(row_info)
    
def do_test(test_name, map_name, out):
    """Performs a single isolated test.
    Automatically logs its results to the out file. 

    Args:
        test_name (string): The name of the test.
        map_name (string): The name of the map.
        out (string): The name of the output file.
    """
    
    if (IS_CSV):
        process_to_csv(test_name, map_name);
    else: # text file
        # Open results file
        result = open('results.txt', 'a');
        
        # Do the test
        start = get_timestamp()
        out_text = os.popen("java -classpath out tests.Tester {} {}".format(test_name, map_name)).read()
        end = get_timestamp()
        
        # Append results
        result.write('##########################\n')
        result.write('Test timestamp is {} - {}:\n'.format(start, end))
        result.write(out_text)
        result.write('\n\n');
        
        # Close file
        result.close()
    
def do_all_tests(maps, out=OUT_FILE, overwrite=True):
    """Performs all the tests for the specified map names.
    Writes all the results to the out file.

    Args:
        maps (list): The list of map names to use.
        out (string, optional): The name of the output file. Defaults to OUT_FILE.
        overwrite (bool, optional): Whether or not to discard the contents of the output file at the start of do_all_tests. Defaults to True.
    """
    
    # Overwrite the old results
    if overwrite:
        open(out, 'w').close()
        with open('result_tests.csv', 'w', newline='') as csvfile:
            fieldnames: list[str] = ['test_name', 'test_file', 'time_taken', 'no_move', 'no_c', 'has_won', 'solution'];
            writer = csv.writer(csvfile);
            writer.writerow(fieldnames)

    # Don't forget to place it in /dist/
    os.popen("javac `find . | grep \\.java$` -d out/ -cp out").read()
    
    # Iterate through them and run tests
    counter = 0
    for map_name in maps:
                
        # Progress update
        counter += 1
        print('Running test {} of {}...'.format(counter, len(maps)))

        # Perform the test
        do_test(map_name, map_name, out);
        
# Run the script        
if __name__ == '__main__':
    
    # Get map names
    MAP_NAMES = get_maps()
    
    # Do all the tests for the map
    do_all_tests(MAP_NAMES)
