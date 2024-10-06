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

COLUMN_COLORS = {
    "time_taken": "#ABE188", 
    "no_move": "#F7EF99", 
    "no_c": "#F1BB87",
    #"sol_len": "#F78E69"
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

def plot_dynamic_grid(col_info: list[tuple[str, str]], df: pd.DataFrame):
    num_plots = len(col_info)
    
    # Define the number of rows and columns based on the number of plots
    cols = 2  # You can change this to control how many plots are in one row
    rows = (num_plots + cols - 1) // cols  # To calculate the number of rows needed
    
    # Create subplots with a grid of (rows x cols)
    fig, axes = plt.subplots(rows, cols, figsize=(10, 5 * rows))
    
    # Flatten the axes array to easily loop over it
    axes = axes.flatten()
    
    # Loop through the pairs and create a scatter plot for each
    for i, (x_ax, y_col) in enumerate(col_info):
        
        if (x_ax == "index"):
            df.sort_index()
            axes[i].scatter(x=df.index, y=df[y_col], color=COLUMN_COLORS[y_col], label=y_col)
            axes[i].set(xlabel="Tests", ylabel=y_col, title = "Statistic for {}".format(y_col))
        else:
            df.sort_values(x_ax);
            axes[i].scatter(x=df[x_ax], y=df[y_col], color=COLUMN_COLORS[y_col], label=y_col)
            axes[i].set(xlabel=x_ax, ylabel=y_col, title="Statistic for {} vs {}".format(x_ax, y_col))
       
    
    # Hide any unused subplots if the number of pairs is less than the grid size
    for j in range(i + 1, len(axes)):
        axes[j].set_visible(False)
    
    # Adjust layout to avoid overlap
    plt.tight_layout()
    
    # Show the plots
    plt.show()

def preprocess_df(df: pd.DataFrame) -> None:
    df["time_taken"] = df["time_taken"].transform(remove_seconds)
    df["time_taken"] = df["time_taken"].astype(float);

def sum_statistics(df: pd.DataFrame) -> None:
    print("")
    for col, color in COLUMN_COLORS.items():
        print("Statistics of " + colored_out(color) + col + RESET_COLORS + ":")
        for name, stat in BASIS_STATS.items():
            print(name, round(df[col].agg(stat), 2))
        print("")
    

    
def analyze_data() -> None:
    warnings.filterwarnings('ignore')
    
    df = pd.read_csv(CSV_FILE);
    df_sucessful  = df[df["has_won"] == True];
    df_failed    = df[df["has_won"] == False];
    
    preprocess_df(df_failed)
    preprocess_df(df_sucessful)
    
    print(df.head())
    print("")

    print("Has won?");
    print("Won  : " + str(df_sucessful.shape[0]))
    print("Fail : " + str(df_failed.shape[0]))
    
    df.reset_index();
    graph_pairs = [("index", "time_taken"), ("index", "no_move"), ("no_move", "time_taken" )]
    
    # Succesful Tests
    df_sucessful.reset_index()
    sum_statistics(df_sucessful)
    plot_dynamic_grid(graph_pairs, df_sucessful);
    
    # Failed Tests
    df_failed.reset_index()
    sum_statistics(df_failed)
    plot_dynamic_grid(graph_pairs, df_failed)
    
    
            
def generate_graph(df: pd.DataFrame, col_name: str, ax: plt.axes):
    """Generates a pyplot graph

    Args:
        df (pd.DataFrame): _description_
        col_name (str): _description_
        ax (plt.axes): _description_
    """
    plt.scatter(df.index, df[col_name], color=COLUMN_COLORS[col_name], label=col_name)
    ax.set(xlabel="index", ylabel=col_name, title = "Statistic for " + col_name)
    
    
    
# Run the script        
if __name__ == '__main__':
    
    if (os.path.dirname != 'tests'):
        os.chdir('tests');
    # run analyzer 
    analyze_data();
