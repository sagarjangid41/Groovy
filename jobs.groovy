job("imagebuilder"){
        description("this job will copy the file in you os version and push image to docker hub")
        scm {
                 github('Rohan123/dockerauto' , 'master')
             }
        triggers {
                scm("* * * * *")
                
        }
         label("s1")


        steps {
        shell('''sudo cp * /html/

sudo docker build -t rsshekhawat/webserver .

sudo docker push rsshekhawat/webserver''')
      }
}

job("pod_managment1"){
        description("this will creat deploymet for website and expose deployment")
        
        triggers {
        upstream {
    upstreamProjects("imagebuilder")
    threshold("Fail")
        }
        }
        label("s1")

        steps {
        shell('''if sudo kubectl get deployment | grep myweb
then
echo " updating"
else
sudo kubectl create deployment myweb --image=rsshekhawat/webserver
sudo kubectl autoscale deployment myweb --min=10 --max=15 --cpu-percent=80
fi

if sudo kubectl get deployment -o wide | grep latest
then 
sudo kubectl set image deployment myweb rsshekhawat/webserver
else
sudo kubectl set image deployment myweb http=rsshekhawat/webserver
fi




if sudo kubectl get service | grep myweb
then 
echo "service exist"
else
sudo kubectl expose deployment myweb --port=80 --type=NodePort
fi ''')
      }
}


job("testing"){
        description("this job will test the env")
        
        triggers {
                upstream {
    upstreamProjects("pod_managment1")
    threshold("Fail")
   } 
        }
        label("s1")


        steps {
        shell('''if sudo kubectl get deployment | grep myweb
then
echo " All good"
else
cd /sagar123/
python3 mail.py

fi
''')
      }
}
