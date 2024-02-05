import pandas as pd
import json

def safe_parse_json(value):
    try:
        return json.loads(value)
    except (json.JSONDecodeError, TypeError):
        return None

def find_differences(file1, file2, common_column):
    df1 = pd.read_csv(file1)
    df2 = pd.read_csv(file2)

    merged_df = pd.merge(df1, df2, on=common_column, how='outer', indicator=True)

    differences = merged_df[merged_df['_merge'] != 'both']

    return merged_df, differences

if __name__ == "__main__":
    file1_path = "judyDemo2.csv"
    file2_path = "judyDemo15.csv"

    common_column_name = "Mutant"

    merged_df, differences = find_differences(file1_path, file2_path, common_column_name)

    if not differences.empty:
        print("Differences found:")
        print(differences.iloc[:, :2].to_string(index=False, header=True))
    else:
        print("CSV files are identical.")

    # Print non-differences
    non_differences = merged_df[merged_df['_merge'] == 'both']

    if not non_differences.empty:
        print("\nNon-differences:")
        print(non_differences.iloc[:, :2].to_string(index=False, header=True))
    else:
        print("All rows are identical.")
