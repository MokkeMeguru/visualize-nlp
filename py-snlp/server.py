import stanfordnlp
from flask import Flask, request, jsonify
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

Pipeline = stanfordnlp.Pipeline(lang='ja')


def refinement_dep_doc(dep_info: tuple):
    """refinement stanfordnlp's output for json format
    """
    return ""


def get_documented_sentences(text: str, category: str):
    """text->annotated words list's list
    """
    assert category in ["dependencies"]

    def _get_documented_sentence(
            sentence_doc: stanfordnlp.pipeline.doc.Sentence):
        """parse annotated sentence (=words list)
        """
        if category == 'dependencies':
            return [
                refinement_dep_doc(dep_info)
                for dep_info in sentence_doc.dependencies
            ]

    doc = Pipeline(text)
    return [
        _get_documented_sentence(sentence_doc)
        for sentence_doc in doc.sentences
    ]


class ParseSnlp(Resource):
    def get(self):
        pass

    def post(self):
        data = request.json
        text: str = data.get('text')
        if data.get('app') == 'parse-snlp' and len(text) >= 1:
            res = {
                'original text': text,
                'type': 'dependencies',
                'result': get_documented_sentences(text, "dependencies")
            }
            return jsonify(res)
        else:
            pass


api.add_resource(ParseSnlp, '/parse-snlp/dependencies')

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=8810, debug=True)
