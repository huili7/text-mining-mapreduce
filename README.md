using the provided ant build script by typing ant bigjar in the project main folder. Copy the resources folder to your local hdfs filesystem. This folder contains components (e.g., models, dictionaries, ...) required for different text mining steps.

myProperty.xml is a configuration file for accessing the dbSNP database located on the mySQL Server (Important: Set username and password in this file; other parameters should be fine)
mutations.txt  contains 767 regular expressions for detecting mutation mentions
lddb contains four dictionaries required for disease name recognition
tim contains different models (e.g., CRF model, sentence boundary, ...) for disease name recognition
Input format
The workflow is currently capable of handling the following input formats:

PubMed citations in XML (see DTD)
PubMed-Central fulltext articles in JATS format (http://dtd.nlm.nih.gov/publishing/)
Arbitrary files provided in the following format: <PDF2TXT PMC="YourID">Document body to analyze </PDF2TXT>