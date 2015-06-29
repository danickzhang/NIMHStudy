# coding=utf-8
import sys

__author__ = 'Andrew Smith'

import argparse
import xml.etree.ElementTree as ET
import glob
import os.path


class SurveyImporter:
    """
    Loads survey and question information from XML files and outputs insert statements
    """

    def __init__(self):
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

    return sql


class SurveyLoader:
    """
    Reads survey and question information from an XML file.
    """

    def __init__(self):
        pass

    def process_file(self, directory, filename, baseid=""):

        tree = ET.parse(os.path.join(directory, os.path.basename(filename)))

        external_files = tree.findall('externalsource')
        questions_xml = tree.findall('category/question')

        data = {"questions": {}, "surveys": {}}

        # follow any external links first
        for external_file in external_files:
            fname = external_file.get('filename')

            processed_file = self.process_file(directory, fname, baseid=external_file.get('baseid'))

            data['questions'].update(processed_file['questions'])
            data['surveys'].update(processed_file['surveys'])

        questions = [(question_xml.get('id'), question_xml.findall('text')[0].text) for question_xml in questions_xml]

        for question in questions:
            data["questions"]["%s%s" % (baseid, question[0])] = question[1]
            # noinspection PyTypeChecker
            data["surveys"].setdefault(os.path.basename(filename), []).append("%s%s" % (baseid, question[0]))

        return data

    def process_directory(self, directory):
        matches = glob.glob("%s/*Parcel.xml" % directory)
        sql = []
        for match in matches:
            sql.extend(process_data(self.process_file(os.path.abspath(directory), match)))

        sql = list(set(sql))
        sql.sort()

        return "\n".join(sql)


if __name__ == '__main__':
    logo = """
███╗   ██╗██╗███╗   ███╗██╗  ██╗
████╗  ██║██║████╗ ████║██║  ██║
██╔██╗ ██║██║██╔████╔██║███████║
██║╚██╗██║██║██║╚██╔╝██║██╔══██║
██║ ╚████║██║██║ ╚═╝ ██║██║  ██║
╚═╝  ╚═══╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝
     XML to SQL Exporter
"""

    sys.stderr.write(logo)
    parser = argparse.ArgumentParser(
        description="Loads NIMH Parcel files and generates SQL insert statements to create the surveys and questions.")
    parser.add_argument("inputDir", help="The directory containing the xml files.  Probably assets/")
    # parser.add_argument("outputFile", help="The path to the output SQL file")

    args = parser.parse_args()

    loader = SurveyLoader()

    print(loader.process_directory(args.inputDir))
