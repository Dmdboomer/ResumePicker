fun main() {
    try {
        val coverLetterText = """
            Cover letter:
Dear Hiring Manager,
I am writing to express my keen interest in the Machine Learning Engineer Intern position in Shanghai, as advertised. My skills and experience align strongly with the requirements outlined in the job description.
As a Statistics student at the University of Waterloo (expected graduation May 2028), I possess a solid foundation in data analysis, statistical modeling, and machine learning. My coursework, including advanced sections in Math and Computer Science, has provided me with a strong understanding of the theoretical underpinnings of AI/ML algorithms.\\n
My experience includes developing and deploying machine learning models, as demonstrated by my personal project, "Game Bot," where I fine-tuned transformer policy/value networks (PyTorch) with self-reinforcement learning and optimized inference for real-time gameplay. Furthermore, my work at Yoocar involved extracting and transforming data to support predictive modeling, enabling the modeling team to optimize chip performance and EV network efficiency. I also have experience with LLM fine-tuning (GPT/LLaMA) and model deployment MLOps.
I am proficient in Python (PyTorch/TensorFlow, Pandas, Scikit-learn), SQL, and have experience with cloud platforms (AWS/Azure, SageMaker). I am also familiar with CI/CD pipelines (Jenkins, GitHub Actions) and deployment technologies (Docker, Kubernetes). My experience with GPU acceleration (CUDA) would be directly applicable to optimizing model performance.\\n
I am eager to contribute my skills and learn from experienced professionals in a dynamic environment. I am a strong team player with excellent communication skills and a commitment to overcoming new challenges.
Thank you for your time and consideration. I have attached my resume for your review and welcome the opportunity to discuss my qualifications further. You can reach me at 204-557-9861 or ghuwin@gmail.com.
Sincerely,
Garry Hu
        """.trimIndent()
        println(PdfGenerator.textToPdf(coverLetterText, "coverLetters/Test.pdf"))

    } catch (e: Exception) {
        println("Error generating PDF: ${e.message}")
    }
}