job("dev-t6-job1"){
  description("Pull files from github repo automatically when some developers push code to github")
  scm{
    github("Priyanshi-Garg/dev-task6-code","master")
  }
  triggers {
    scm("* * * * *")
  }
  steps{
    shell('''if ls / | grep task6-ws
then
sudo cp -rf * /task6-ws
else
sudo mkdir /task6-ws
sudo cp -rf * /task6-ws
fi  
''')
  }
}

job("dev-t6-job2"){
  description("By looking at the code it will launch the deployment of respective webserver and the deployment will launch webserver, create PVC and expose the deployment")
  
  authenticationToken('deploy')
  
  triggers {
    upstream("dev-t6-job1", "SUCCESS")
  }
  steps{
shell('''cd /task6-ws
if ls | grep ".html"
then
if ls | grep ws-html
then
sudo rm -rvf /task6-ws/ws-html
sudo mkdir /task6-ws/ws-html
else
sudo mkdir /task6-ws/ws-html
fi
sudo cp -rvf /task6-ws/*.html /task6-ws/ws-html

sudo kubectl delete -f task-6.yml
sudo kubectl create -f task-6.yml
sleep 20
cd /task6-ws/ws-html
ls
sshpass -p "tcuser" scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -r * docker@192.168.99.105:/home/docker/devops-task6

fi 
''')
  }
}

job("dev-t6-job3"){
    description("Testing JOB")
	triggers{
		upstream('dev-t6-job2' , 'SUCCESS')
	}
	steps{
		shell('''status=$(curl -o /dev/null  -s  -w "%{http_code}"  http://192.168.99.105:30000)
if [ $status == 200 ]
then
exit 0
else
exit 1
fi
''')
	}
}

job("dev-t6-job4 "){
  description("This Job is created for monitoring of the container and to launch another if the existing fails.")

triggers {
    upstream("dev-t6-job3", "FAILURE")
  }
  steps{
    shell('''
    sudo python3 /root/mail.py
    sudo curl -I --user admin:pg1103 http://192.168.99.102:8080//job/dev-t6-job2/build?token=deploy
''')
}
}


