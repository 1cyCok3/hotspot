package model

case class Article(abstractWords: Seq[String],
                   authors: Seq[Author],
                   citationNumber: Long,
                   doi: String,
                   fieldOfStudy: Seq[FieldOfStudy],
                   id: String,
                   references: Seq[String],
                   title: String,
                   venue: Venue,
                   year: Long) {

  override def toString: String =
    s"""
      |title: $title
      |article_id: $id
      |authors: $authors
      |abstractWords: $abstractWords
      |citationNumber: $citationNumber
      |doi: $doi
       |fieldOfStudy: $fieldOfStudy
       |references: $references
       |year: $year
    """.stripMargin
}
