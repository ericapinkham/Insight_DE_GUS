



a = """SUM(CASE WHEN package_name = '{0}' THEN usage_count ELSE 0 END) "{0}")""".format("Data")

print(a)
