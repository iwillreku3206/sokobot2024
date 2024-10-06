'''

This is so to analyze the data from the tests
'''
import os
import math
import re
import warnings
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.widgets import Button

RESET_COLORS = "\033[0m" 

CSV_FILE = "result_tests.csv"

COL_INFO = {
    "time_taken": "#A66E38", 
    "no_move": "#FFAD60", 
    "no_c": "#FFEEAD"  
}

BASIS_STATS = {
    "Max    :" : "max",
    "Mean   :" : "mean",
    "Min    :" : "min",
    "Sum    :" : "sum"
}

def hex_to_rgb(hex_code: str) -> tuple[int, int, int]:
    """Turns hexcode to rgb

    Args:
        hex_code (str): hexcode that starts with #

    Returns:
        tuple(int, int, int): a tuple containing rgb information
    """
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i + 2], 16) for i in (0, 2, 4))

def rgb_to_ansi_rgb(r, g, b):
    """Turns rgb values to ANSI escape codes

    Args:
        r (int): red
        g (int): green
        b (int): blue

    Returns:
        str: Equivalent Ansi Escape code.
    """
    return f"\033[38;2;{r};{g};{b}m"

def colored_out(hex_code):
    """_summary_

    Args:
        hex_code (str): hexcode that starts with #

    Returns:
        str: Equivalent Ansi Escape code.
    """
    r, g, b = hex_to_rgb(hex_code)
    ansi_color = rgb_to_ansi_rgb(r, g, b)
    return ansi_color;

def remove_seconds(input: str):
    """Used as an aggregate function to strip the seconds unit

    Args:
        input (str): numeric containing s at the end

    Returns:
        str: String with removed string unit
    """
    return input.rstrip("s"); 

def plot_dynamic_grid(col_info: list[str], df: pd.DataFrame):
    num_plots = len(col_info)
    
    # Define the number of rows and columns based on the number of plots
    cols = 2  # You can change this to control how many plots are in one row
    rows = (num_plots + cols - 1) // cols  # To calculate the number of rows needed
    
    # Create subplots with a grid of (rows x cols)
    fig, axes = plt.subplots(rows, cols, figsize=(10, 5 * rows))
    
    # Flatten the axes array to easily loop over it
    axes = axes.flatten()
    
    # Loop through the pairs and create a scatter plot for each
    for i, col in enumerate(col_info):
        df.sort_index()
        axes[i].scatter(x=df.index, y=df[col], color=COL_INFO[col], label=col)
        axes[i].set(xlabel="Tests", ylabel=col, title = "Statistic for " + col)
    
    # Hide any unused subplots if the number of pairs is less than the grid size
    for j in range(i + 1, len(axes)):
        axes[j].set_visible(False)
    
    # Adjust layout to avoid overlap
    plt.tight_layout()
    
    # Show the plots
    plt.show()

def analyze_data() -> None:
    df = pd.read_csv(CSV_FILE);
    
    
    # Those that are successful
    has_won_true = df["has_won"] == True;
    has_failed   = df["has_won"] == False;
    df_failed    = df[has_failed];
    df_sucessful = df[has_won_true];
    
    warnings.filterwarnings('ignore')
    df_sucessful["time_taken"] = df_sucessful["time_taken"].transform(remove_seconds)
    df_sucessful["time_taken"] = df_sucessful["time_taken"].astype(float);
    df_failed["time_taken"] = df_failed["time_taken"].transform(remove_seconds)
    df_failed["time_taken"] = df_failed["time_taken"].astype(float);
    
    print(df.head())
    
    # Some important statistics for each succesful runs
    print("")
    for col, color in COL_INFO.items():
        print("Statistics of " + colored_out(color) + col + RESET_COLORS + ":")
        for name, stat in BASIS_STATS.items():
            print(name, round( df_failed[col].agg(stat), 2))
        print("")
    
    df.reset_index();
    
    graph_pairs = ["time_taken", "no_c", "no_move"]
    plot_dynamic_grid(graph_pairs, df_sucessful);
    plot_dynamic_grid(graph_pairs, df_failed)
            
def generate_graph(df: pd.DataFrame, col_name: str, ax: plt.axes):
    """Generates a pyplot graph

    Args:
        df (pd.DataFrame): _description_
        col_name (str): _description_
        ax (plt.axes): _description_
    """
    plt.scatter(df.index, df[col_name], color=COL_INFO[col_name], label=col_name)
    ax.set(xlabel="index", ylabel=col_name, title = "Statistic for " + col_name)
    
    
    
# Run the script        
if __name__ == '__main__':
    
    if (os.path.dirname != 'tests'):
        os.chdir('tests');
    # run analyzer 
    analyze_data();
