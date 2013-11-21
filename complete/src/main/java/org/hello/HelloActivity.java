package org.hello;

import org.hello.Greeting;
import org.hello.R;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelloActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_layout);
    }

    @Override
    public void onStart() {
        super.onStart();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Greeting greeting = restTemplate.getForObject("http://rest-service.guides.spring.io/greeting", Greeting.class);

        TextView textView = (TextView) this.findViewById(R.id.greeting_id);
        textView.setText(greeting.getId());

        textView = (TextView) this.findViewById(R.id.greeting_content);
        textView.setText(greeting.getContent());
    }

}
