job("devops_task6_job1"){
  description("Pull the data from github repo automatically when some developers push code to github")
  scm{
    github("Priyanshi-Garg/dev-task6.git","master")
  }
  triggers {
    scm("* * * * *")
  }
  steps{
    shell('''if ls / | grep task6_devops
then
sudo cp -rf * /task6_devops
else
sudo mkdir /task6_devops
sudo cp -rf * /task6_devops
fi  
''')
  }
}
