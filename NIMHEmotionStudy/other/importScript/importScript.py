__author__ = 'Andrew Smith'

'''
Loads survey and question information from XML files and outputs insert statements
'''
class SurveyImporter:
    def __init__(self):
        pass

'''
Reads survey and question information from an XML file.
'''
class SurveyLoader:
    def __init__(self):
        pass

    def process_directory(dir):
        pass


    def process_file(filename, baseid=""):
        pass



def insert_survey(survey_id, survey_name):
    return "insert into survey(surveyID, name) values('%s', '%s')" % (survey_id, survey_name)

def insert_question(question_id, question_text):
    return "insert into question(questionID, text) values('%s', '%s')" % (question_id, question_text)

def insert_question_on_survey(survey_id, question_id):
    return "insert into questionOnSurvey(surveyID, questionID) values ('%s', '%s')" % (survey_id, question_id)

def process_questions(questions):
    return [insert_question(question_id, question_text) for (question_id, question_text) in questions]

def process_surveys(surveys):
    sql = [insert_survey(survey_name, survey_name) for survey_name in surveys.keys()]

    for survey_name in surveys.keys():
        sql.extend([insert_question_on_survey(survey_name, question_id) for question_id in surveys[survey_name]])

    return sql

def process_data(data):
    sql = process_questions(data["questions"].items())
    sql.extend(process_surveys(data["surveys"]))

    return ";\n".join(sql)

if __name__ == '__main__':
    a = {
        "questions": {
            "questionid1": "questiontext1",
            "questionid2" : "questiontext2",
            "questionid3" : "questiontext3"
        },
        "surveys": {
            "survey1": ["questionid1", "questionid2"],
            "survey2": ["questionid3"]
        }
    }

    print process_data(a)

