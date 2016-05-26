records = LOAD './web-Google.txt' USING PigStorage('\t') AS (fromNode: int, toNode: int);
grouped = GROUP records BY toNode;
results = foreach grouped generate group, COUNT(records);
STORE results INTO './linkcount';